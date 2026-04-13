import request from '../utils/request'

/**
 * 获取用户推荐设施列表
 */
export function getUserRecommendations(userId) {
  return request({
    url: `/recommend/user/${userId}`,
    method: 'get'
  })
}

/**
 * 获取推荐列表（别名）
 */
export function getRecommendFacilities(userId) {
  return getUserRecommendations(userId)
}

/**
 * 重新生成用户推荐
 */
export function regenerateRecommendations(userId) {
  return request({
    url: `/recommend/user/${userId}/regenerate`,
    method: 'post'
  })
}

/**
 * 手动刷新缓存（别名）
 */
export function refreshRecommendations(userId) {
  return regenerateRecommendations(userId)
}

/**
 * 获取热门设施推荐
 */
export function getHotRecommendations(limit = 10) {
  return request({
    url: '/recommend/hot',
    method: 'get',
    params: { limit }
  })
}

/**
 * 热门设施（别名）
 */
export function getHotFacilities(limit = 10) {
  return getHotRecommendations(limit)
}