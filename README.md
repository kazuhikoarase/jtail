jtail
=====

Copyright (c) Kazuhiko Arase

URL: http://www.d-project.com/

Licensed under the MIT license:
  http://www.opensource.org/licenses/mit-license.php

### Usage
```
java -jar jtail.jar /path/to/myapp.log
```

Filtering lines by regular expression.
```
java -jar jtail.jar /path/to/myapp.log Error|Exception
```

To exclude lines, write regular expression starts with '!'.
```
java -jar jtail.jar /path/to/myapp.log !Error|Exception
```
