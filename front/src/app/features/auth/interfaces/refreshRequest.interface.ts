export interface RefreshRequest {
    refreshToken: string;
}

export interface RefreshResponse {
    token: string;
    refreshToken: string;
}
