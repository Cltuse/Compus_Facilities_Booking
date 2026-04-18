const trimTrailingSlash = (value) => value.replace(/\/+$/, '')

export const apiBaseUrl = trimTrailingSlash(
  import.meta.env.VITE_API_BASE_URL || '/api'
)
