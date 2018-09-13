import org.scalatest.{Matchers, WordSpec}

import sangria.ast.Document
import sangria.macros._
import sangria.execution.Executor
import sangria.execution.deferred.DeferredResolver
import sangria.marshalling.circe._

import io.circe._
import io.circe.parser._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import SchemaDefinition.TrayIoSchema

class SchemaSpec extends WordSpec with Matchers {
  "TrayIoSchema Schema" should {
    "find a workflow" in {
      val query =
        graphql"""
         query getAWorkflow {
           workflow(id: 0) {
             id,
             steps
           }
         }
      """

      executeQuery(query) should be (parse(
        """
        {
          "data": {
            "workflow": {
              "id": 0,
              "steps": 1
            }
          }
        }
        """).right.get)
    }

    "find a workflow execution" in {
      val query =
        graphql"""
         query getAWorkflowExecution {
           workflowExecution(id: 0) {
             id,
             workflow,
             stepIndex
           }
         }
      """

      executeQuery(query) should be (parse(
        """
        {
          "data": {
            "workflowExecution": {
              "id": 0,
              "workflow": 0,
              "stepIndex": 0
            }
          }
        }
        """).right.get)
    }

    "insert a workflow" in {
      val query =
        graphql"""
        mutation insertWorkflow {
          addWorkflow(id:2, steps:3) {
            id,
            steps
          }
        }
      """

      executeQuery(query) should be (parse(
        """
        {
          "data": {
            "addWorkflow": {
              "id": 2,
              "steps": 3
            }
          }
        }
        """).right.get)

      val query2 =
        graphql"""
         query getAWorkflow {
           workflow(id: 2) {
             id,
             steps
           }
         }
      """

      executeQuery(query2) should be (parse(
        """
        {
          "data": {
            "workflow": {
              "id": 2,
              "steps": 3
            }
          }
        }
        """).right.get)
    }
  }
  def executeQuery(query: Document, vars: Json = Json.obj()) = {
    val futureResult = Executor.execute(TrayIoSchema, query,
      variables = vars,
      userContext = new TrayRepo,
      deferredResolver = DeferredResolver.fetchers(SchemaDefinition.workflows, SchemaDefinition.workflowExecutions))

    Await.result(futureResult, 10.seconds)
  }
}
