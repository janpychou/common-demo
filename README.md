# user-group
项目打包命令： mvn clean package -DskipTests  
程序启动：./start.sh  
支持的参数：-bindip 192.168.0.1 -port 2628  

# 功能列表
1. 通过Jersey提供Rest API功能
2. 使用jetty提供http服务
3. 使用Jersey默认的Grizzly提供http服务
4. 使用Jedis读写redis，同时支持单节点和集群模式
5. 使用Jersey的客户端，调用RestAPI接口
