FROM openjdk:25-rc-jdk-trixie

RUN apt-get update \
    && apt-get install -y maven \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /workspace

CMD ["/bin/bash"]
