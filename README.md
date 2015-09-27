# colt-build
Meta-repository for COLT modules

# Requirements for build
- gradle 2.7 or greater (https://gradle.org/)
- jdk 1.8.0_60 or greater (http://www.oracle.com/technetwork/java/javase/overview/index.html)
- groovy 2.4.4 or greater (http://www.groovy-lang.org/download.html)

# How to build COLT?
- install 'Inno Setup' from http://www.jrsoftware.org/isdl.php#stable
- fork https://github.com/code-orchestra/colt-as
- fork https://github.com/code-orchestra/colt-core
- fork https://github.com/code-orchestra/colt-build
</br>$ git clone https://github.com/**YourLogin**/colt-build colt-build
</br>$ cd colt-build
</br>$ copy gradle.properties.sample gradle.properties
</br>$ start gradle.properties
- edit 
  </br>COLT_AS_GIT = git@github.com:**YourLogin**/colt-as.git
  </br>COLT_CORE_GIT = git@github.com:**YourLogin**/colt-core.git
- save gradle.properties
- Run IDEA
![Import project](http://service.crazypanda.ru/v/clip2net/Z/o/5Z7DJ01o0l.png)
![Select directory of colt-build](http://service.crazypanda.ru/v/clip2net/i/b/lzkTibPrLC.png)
![Import project from external model](http://service.crazypanda.ru/v/clip2net/u/G/X59LAfmIhx.png)
![Setup project](http://service.crazypanda.ru/v/clip2net/N/1/7suehNUS89.png)
- press alt+F12
</br>$ gradle build
</br>$ gradle run
