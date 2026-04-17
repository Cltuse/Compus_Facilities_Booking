const TOKEN_KEY = 'accessToken'
const USER_KEY = 'userInfo'
const EXPIRES_KEY = 'accessTokenExpiresAt'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function getUserInfo() {
  const raw = localStorage.getItem(USER_KEY)
  return raw ? JSON.parse(raw) : null
}

export function setUserInfo(user) {
  if (!user) {
    localStorage.removeItem(USER_KEY)
    return
  }

  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

export function updateStoredUserInfo(patch) {
  const currentUser = getUserInfo()

  if (!currentUser) {
    return null
  }

  const nextUser = {
    ...currentUser,
    ...patch
  }

  setUserInfo(nextUser)
  return nextUser
}

export function isAuthenticated() {
  const token = getToken()
  const expiresAt = Number(localStorage.getItem(EXPIRES_KEY) || '0')

  if (!token) {
    return false
  }

  if (expiresAt > 0 && Date.now() >= expiresAt) {
    clearAuth()
    return false
  }

  return true
}

export function setAuth(authPayload) {
  const token = authPayload?.token
  const user = authPayload?.user
  const expiresIn = Number(authPayload?.expiresIn || 0)

  if (!token || !user) {
    clearAuth()
    return
  }

  localStorage.setItem(TOKEN_KEY, token)
  setUserInfo(user)

  if (expiresIn > 0) {
    localStorage.setItem(EXPIRES_KEY, String(Date.now() + expiresIn))
  } else {
    localStorage.removeItem(EXPIRES_KEY)
  }
}

export function clearAuth() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
  localStorage.removeItem(EXPIRES_KEY)
}
