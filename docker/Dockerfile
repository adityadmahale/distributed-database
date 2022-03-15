FROM ubuntu:20.04

RUN apt-get update && apt-get install -y openssh-server openjdk-17-jre openjdk-17-jdk
RUN mkdir /var/run/sshd
RUN echo 'root:root' | chpasswd
RUN echo "PermitRootLogin yes" >> /etc/ssh/sshd_config
RUN sed 's@session\s*required\s*pam_loginuid.so@session optional pam_loginuid.so@g' -i /etc/pam.d/sshd
COPY target/DistributedDatabase.jar .
EXPOSE 22
CMD ["/usr/sbin/sshd", "-D"]