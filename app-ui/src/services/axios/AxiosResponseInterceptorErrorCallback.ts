import axios from 'axios'
import appConfig from '@/configs/app.config'
import { setAccessToken } from '@/services/tokenMemoryStore'
import { useSessionUser } from '@/store/authStore'
import type { AxiosError, AxiosInstance, InternalAxiosRequestConfig } from 'axios'

type RetryableConfig = InternalAxiosRequestConfig & { _retry?: boolean }

let isRefreshing = false
let pendingRequests: { resolve: (token: string) => void; reject: (error: unknown) => void }[] = []

const AxiosResponseInterceptorErrorCallback =
    (axiosInstance: AxiosInstance) => async (error: AxiosError) => {
        const { response } = error
        const reqConfig = error.config as RetryableConfig | undefined

        if (response?.status === 401 && reqConfig && !reqConfig._retry) {
            reqConfig._retry = true

            if (isRefreshing) {
                return new Promise((resolve, reject) => {
                    pendingRequests.push({
                        resolve: (token: string) => {
                            reqConfig.headers['Authorization'] = `Bearer ${token}`
                            resolve(axiosInstance.request(reqConfig))
                        },
                        reject,
                    })
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
                pendingRequests.forEach(({ resolve }) => resolve(newToken))
                pendingRequests = []
                reqConfig.headers['Authorization'] = `Bearer ${newToken}`
                return axiosInstance.request(reqConfig)
            } catch {
                setAccessToken(null)
                pendingRequests.forEach(({ reject: r }) => r(error))
                pendingRequests = []
                useSessionUser.getState().setUser({})
                useSessionUser.getState().setSessionSignedIn(false)
            } finally {
                isRefreshing = false
            }
        }

        return Promise.reject(error)
    }

export default AxiosResponseInterceptorErrorCallback
