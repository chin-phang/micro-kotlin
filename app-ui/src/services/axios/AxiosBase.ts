import axios from 'axios'
import AxiosRequestIntrceptorConfigCallback from './AxiosRequestIntrceptorConfigCallback'
import appConfig from '@/configs/app.config'
import { getAccessToken, setAccessToken } from '@/services/tokenMemoryStore'
import { useSessionUser } from '@/store/authStore'
import type { AxiosError, InternalAxiosRequestConfig } from 'axios'

type RetryableConfig = InternalAxiosRequestConfig & { _retry?: boolean }

const AxiosBase = axios.create({
    timeout: 60000,
    baseURL: appConfig.apiPrefix,
    withCredentials: true,
})

let isRefreshing = false
let pendingRequests: ((token: string) => void)[] = []

AxiosBase.interceptors.request.use(
    (config) => AxiosRequestIntrceptorConfigCallback(config),
    (error) => Promise.reject(error),
)

AxiosBase.interceptors.response.use(
    (response) => response,
    async (error: AxiosError) => {
        const { response } = error
        const reqConfig = error.config as RetryableConfig | undefined

        if (response?.status === 401 && reqConfig && !reqConfig._retry) {
            reqConfig._retry = true

            if (isRefreshing) {
                return new Promise((resolve, reject) => {
                    pendingRequests.push((token) => {
                        reqConfig.headers['Authorization'] = `Bearer ${token}`
                        resolve(AxiosBase.request(reqConfig))
                    })
                    setTimeout(() => {
                        if (!getAccessToken()) reject(error)
                    }, 10000)
                })
            }

            isRefreshing = true
            try {
                const resp = await axios.post(
                    `${appConfig.apiPrefix}/refresh`,
                    {},
                    { withCredentials: true },
                )
                const newToken: string = resp.data.accessToken
                setAccessToken(newToken)
                pendingRequests.forEach((cb) => cb(newToken))
                pendingRequests = []
                reqConfig.headers['Authorization'] = `Bearer ${newToken}`
                return AxiosBase.request(reqConfig)
            } catch {
                setAccessToken(null)
                pendingRequests = []
                useSessionUser.getState().setUser({})
                useSessionUser.getState().setSessionSignedIn(false)
            } finally {
                isRefreshing = false
            }
        }

        return Promise.reject(error)
    },
)

export default AxiosBase
