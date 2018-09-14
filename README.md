# Docker

To run the server with docker, execute:

```
sbt docker:publishLocal
docker run --rm -p8080:8080 graphql-example:0.1.0-SNAPSHOT
```

Then open [http://localhost:8080](http://localhost:8080)

# graphql-example

An example [GraphQL](https://graphql.org) server written with [akka-http](https://github.com/akka/akka-http), [circe](https://github.com/circe/circe) and [sangria](https://github.com/sangria-graphql/sangria).

After starting the server with

```bash
sbt run

# or, if you want to watch the source code changes
 
sbt ~reStart
```

you can run queries interactively using [graphql-playground](https://github.com/prisma/graphql-playground) by opening [http://localhost:8080](http://localhost:8080) in a browser or query the `/graphql` endpoint directly. The HTTP endpoint follows [GraphQL best practices for handling the HTTP requests](http://graphql.org/learn/serving-over-http/#http-methods-headers-and-body).

Here are one example of the queries you can make:

```bash
$ curl -X POST localhost:8080/graphql \
  -H "Content-Type:application/json" \
  -d '{"query": "{workflow(id: 0) {id, steps }}"}'
```

this gives back the requested workflow:

```json
{
  "data": {
    "workflow":{
      "id":0,
      "steps":1
    }
  }
}
```

# Tests

Run `sbt test`
