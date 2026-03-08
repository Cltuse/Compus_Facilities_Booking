/**
 * 日期时间工具函数
 */

/**
 * 格式化日期时间
 * @param {string|Date} dateTime - 日期时间字符串或Date对象
 * @returns {string} 格式化后的日期时间字符串
 */
export const formatDateTime = (dateTime) => {
  if (!dateTime) return '';
  
  const date = new Date(dateTime);
  
  // 检查日期是否有效
  if (isNaN(date.getTime())) {
    return '';
  }
  
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  });
};

/**
 * 格式化日期（仅日期部分）
 * @param {string|Date} dateTime - 日期时间字符串或Date对象
 * @returns {string} 格式化后的日期字符串
 */
export const formatDate = (dateTime) => {
  if (!dateTime) return '';
  
  const date = new Date(dateTime);
  
  // 检查日期是否有效
  if (isNaN(date.getTime())) {
    return '';
  }
  
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  });
};

/**
 * 格式化时间（仅时间部分）
 * @param {string|Date} dateTime - 日期时间字符串或Date对象
 * @returns {string} 格式化后的时间字符串
 */
export const formatTime = (dateTime) => {
  if (!dateTime) return '';
  
  const date = new Date(dateTime);
  
  // 检查日期是否有效
  if (isNaN(date.getTime())) {
    return '';
  }
  
  return date.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  });
};

/**
 * 获取相对时间描述（如：刚刚、5分钟前、2小时前等）
 * @param {string|Date} dateTime - 日期时间字符串或Date对象
 * @returns {string} 相对时间描述
 */
export const getRelativeTime = (dateTime) => {
  if (!dateTime) return '';
  
  const date = new Date(dateTime);
  
  // 检查日期是否有效
  if (isNaN(date.getTime())) {
    return '';
  }
  
  const now = new Date();
  const diffInSeconds = Math.floor((now - date) / 1000);
  
  if (diffInSeconds < 60) {
    return '刚刚';
  } else if (diffInSeconds < 3600) {
    return `${Math.floor(diffInSeconds / 60)}分钟前`;
  } else if (diffInSeconds < 86400) {
    return `${Math.floor(diffInSeconds / 3600)}小时前`;
  } else if (diffInSeconds < 2592000) {
    return `${Math.floor(diffInSeconds / 86400)}天前`;
  } else {
    return formatDate(date);
  }
};

/**
 * 检查日期是否在今天
 * @param {string|Date} dateTime - 日期时间字符串或Date对象
 * @returns {boolean} 是否在今天
 */
export const isToday = (dateTime) => {
  if (!dateTime) return false;
  
  const date = new Date(dateTime);
  const today = new Date();
  
  return date.toDateString() === today.toDateString();
};

/**
 * 检查日期是否在昨天
 * @param {string|Date} dateTime - 日期时间字符串或Date对象
 * @returns {boolean} 是否在昨天
 */
export const isYesterday = (dateTime) => {
  if (!dateTime) return false;
  
  const date = new Date(dateTime);
  const yesterday = new Date();
  yesterday.setDate(yesterday.getDate() - 1);
  
  return date.toDateString() === yesterday.toDateString();
};