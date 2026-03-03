// 角色映射配置
const roleMapping = {
    'ADMIN': '管理员',
    'MAINTAINER': '维护人员',
    'TEACHER': '教师',
    'STUDENT': '学生'
};

// 获取角色显示名称
export const getRoleDisplayName = (role) => {
    return roleMapping[role] || '用户';
};

// 获取所有角色选项（用于下拉选择）
export const getRoleOptions = () => {
    return Object.entries(roleMapping).map(([value, label]) => ({
        value,
        label
    }));
};

export default roleMapping;