#mvn install:install-file -Dfile=ojdbc-10.2.0.5.0.jar -DartifactId=ojdbc14 -Dversion=10.2.0.5.0 -DgroupId=com.oracle -Dpackaging=jar -DlocalRepositoryPath=m2
mvn install:install-file \
-Dfile=ojdbc-10.2.0.5.0.jar \
-DartifactId=ojdbc14 \
-Dversion=10.2.0.5.0 \
-DgroupId=com.oracle \
-Dpackaging=jar \
-DlocalRepositoryPath=$HOME/.m2/repository
