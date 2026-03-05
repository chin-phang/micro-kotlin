import { useSessionUser } from '@/store/authStore'
import type { AxiosError } from 'axios'

const unauthorizedCode = [401, 419, 440]

const AxiosResponseInterceptorErrorCallback = (error: AxiosError) => {
    const { response } = error

    if (response && unauthorizedCode.includes(response.status)) {
        useSessionUser.getState().setUser({})
        useSessionUser.getState().setSessionSignedIn(false)
    }
}

export default AxiosResponseInterceptorErrorCallback
