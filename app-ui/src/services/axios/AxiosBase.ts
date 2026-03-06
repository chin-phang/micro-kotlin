import axios from 'axios'
import AxiosRequestInterceptorConfigCallback from './AxiosRequestInterceptorConfigCallback'
import AxiosResponseInterceptorErrorCallback from './AxiosResponseInterceptorErrorCallback'
import appConfig from '@/configs/app.config'

const AxiosBase = axios.create({
    timeout: 60000,
    baseURL: appConfig.apiPrefix,
    withCredentials: true,
})

AxiosBase.interceptors.request.use(
    (config) => AxiosRequestInterceptorConfigCallback(config),
    (error) => Promise.reject(error),
)

AxiosBase.interceptors.response.use(
    (response) => response,
    AxiosResponseInterceptorErrorCallback(AxiosBase),
)

export default AxiosBase
