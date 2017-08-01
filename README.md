# ModularizationArchitecture-demo

本Demo Fork自ModularizationArchitecture

原作者地址: https://github.com/SpinyTech/ModularizationArchitecture

## 初衷

这是第一次接触组件化模块化是接触的选择的路由.

## 修改&重构

根据真实在项目中的使用进行了重构,与原框架差别较大

主要修改:

1,移除了Router的MaApplication.把功能拆除到MaApplicationLogic和PriorityLogicUtils中,减少了侵入性

2,移除了MaProvider的设置.参考ARouter加入了Group,以"/{组}/{动作}"来定位Action

3,尝试引入本地Maven,加快编译速度

4,添加了全局的Gradle配置,方便多模块统一配置

#TODO

添加AOP编辑时注解



---
```
 记一次Router框架初步重构
```