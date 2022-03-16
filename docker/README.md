### Docker:

Steps:
1. mvn clean install
2. cd docker
3. docker-compose up -d
4. Get container IPs:
   docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' node1
   docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' node2
5. ssh root@node_ip
