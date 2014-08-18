jtail
=====

Copyright (c) Kazuhiko Arase

URL: http://www.d-project.com/

Licensed under the MIT license:
  http://www.opensource.org/licenses/mit-license.php

### Usage
```
java -jar jtail.jar myapp.log
```

filtering(include)
```
java -jar jtail.jar myapp.log Error|Exception
```

filtering(exclude)
```
java -jar jtail.jar myapp.log !Error|Exception
```
