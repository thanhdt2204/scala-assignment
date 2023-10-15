#### 1. Run application
```bash
sbt run
```

#### 2. Run Unit Test
```bash
sbt clean test
```
or 
```bash
sbt clean coverage test coverageReport
```

#### 3. Run Integration Test
```bash
sbt clean integration/test