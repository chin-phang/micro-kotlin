export type SignInCredential = {
    email: string
    password: string
}

export type SignInResponse = {
    accessToken: string
    userId: number
    userName: string
    email: string
    authority: string[]
}

export type SignUpResponse = SignInResponse

export type SignUpCredential = {
    userName: string
    email: string
    password: string
}

export type ForgotPassword = {
    email: string
}

export type ResetPassword = {
    password: string
}

export type AuthRequestStatus = 'success' | 'failed' | ''

export type AuthResult = Promise<{
    status: AuthRequestStatus
    message: string
}>

export type User = {
    userId?: string | null
    avatar?: string | null
    userName?: string | null
    email?: string | null
    authority?: string[]
}

export type OauthSignInCallbackPayload = {
    onSignIn: (user?: User) => void
    redirect: () => void
}
