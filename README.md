# corant

[![License](https://img.shields.io/:license-Apache2-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/20220b8b4baf4a93a0a868ec80d1468c)](https://app.codacy.com/app/finesoft/corant?utm_source=github.com&utm_medium=referral&utm_content=finesoft/corant&utm_campaign=Badge_Grade_Dashboard) [![Join the chat at https://gitter.im/corant-project/community](https://badges.gitter.im/corant-project/community.svg)](https://gitter.im/corant-project/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Microservice stack with CDI MicroProfile.

Corant is a microservice development stack. Use Maven for builds, base on J2SE use CDI as container.
We have 7 projects under the corant project.
1. corant-boms is the maven dependence descriptions that are used in corant.
2. corant-demo is a demo project
3. corant-devops is the corant build and testing project. include maven build plugins and framework testing.
4. corant-kernel include application configuration, boostrap and CDI integration, for now we use WELD as CDI container and Microprofile config as configuration processer.
5. corant-shared is the global shared project, contains utility classes for resource handling, string handling, reflection, object conversion, assertion, etc. Most projects rely on this.
6. corant-suites is integration project. This project includes many subprojects that integrate JEE or other open source components to make integration and development easier. For now we support JTA/JPA/JMS/JNDI/JAXRS and app-server etc.
7. corant-asosat is a development use case, include some domains knowledge. 
