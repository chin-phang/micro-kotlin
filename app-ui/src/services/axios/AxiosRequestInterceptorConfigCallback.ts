import { getAccessToken } from '@/services/tokenMemoryStore'
import type { InternalAxiosRequestConfig } from 'axios'

const AxiosRequestIntrceptorConfigCallback = (
    config: InternalAxiosRequestConfig,
) => {
    const token = getAccessToken()
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
}

export default AxiosRequestIntrceptorConfigCallback
