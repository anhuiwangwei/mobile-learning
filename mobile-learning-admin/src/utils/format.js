import dayjs from 'dayjs'

/**
 * 格式化日期时间
 * @param {Date|string|number} date - 日期对象、字符串或时间戳
 * @param {string} format - 格式化模板，默认 'YYYY-MM-DD HH:mm:ss'
 * @returns {string} 格式化后的日期字符串
 */
export const formatDate = (date, format = 'YYYY-MM-DD HH:mm:ss') => {
  if (!date) return '-'
  return dayjs(date).format(format)
}

/**
 * 格式化日期（仅日期部分）
 * @param {Date|string|number} date
 * @returns {string} YYYY-MM-DD 格式
 */
export const formatDateOnly = (date) => {
  return formatDate(date, 'YYYY-MM-DD')
}

/**
 * 格式化时间（仅时间部分）
 * @param {Date|string|number} date
 * @returns {string} HH:mm:ss 格式
 */
export const formatTimeOnly = (date) => {
  return formatDate(date, 'HH:mm:ss')
}

/**
 * 格式化相对时间
 * @param {Date|string|number} date
 * @returns {string} 如：刚刚、几分钟前、几小时前等
 */
export const formatRelative = (date) => {
  if (!date) return '-'
  return dayjs(date).fromNow()
}

/**
 * 格式化时长（秒转为可读格式）
 * @param {number} seconds - 秒数
 * @returns {string} 如：1 小时 30 分、5 分 30 秒
 */
export const formatDuration = (seconds) => {
  if (!seconds && seconds !== 0) return '-'

  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60

  if (hours > 0) {
    return `${hours}小时${minutes}分`
  } else if (minutes > 0) {
    return `${minutes}分${secs}秒`
  } else {
    return `${secs}秒`
  }
}
