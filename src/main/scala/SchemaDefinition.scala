import sangria.execution.deferred.{Fetcher, HasId}
import sangria.schema._

import scala.concurrent.Future

/**
 * Defines a GraphQL schema for the current project
 */
object SchemaDefinition {
  /**
    * Resolves the lists of workflows. These resolutions are batched and
    * cached for the duration of a query.
    */
  val workflows = Fetcher.caching(
    (ctx: TrayRepo, ids: Seq[Int]) =>
      Future.successful(
        ids.flatMap(
          id => ctx.getWorkflow(id)
        )
      )
  )(HasId(_.id))

  /**
    * Resolves the lists of workflow executions. These resolutions are batched and
    * cached for the duration of a query.
    */
  val workflowExecutions = Fetcher.caching(
    (ctx: TrayRepo, ids: Seq[Int]) =>
      Future.successful(
        ids.flatMap(
          id => ctx.getWorkflow(id)
        )
      )
  )(HasId(_.id))

  val Workflow: ObjectType[TrayRepo, Workflow] =
    ObjectType(
      "Workflow",
      "A Workflow",
      () => fields[TrayRepo, Workflow](
        Field("id", IntType,
          Some("id of the workflow"),
          resolve = _.value.id),
        Field("steps", OptionType(IntType),
          Some("number of steps of the workflow"),
          resolve = _.value.steps))
      )

  val WorkflowExecution: ObjectType[TrayRepo, WorkflowExecution] =
    ObjectType(
      "WorkflowExecution",
      "A WorkflowExecution",
      () => fields[TrayRepo, WorkflowExecution](
        Field("id", IntType,
          Some("id of the execution"),
          resolve = _.value.id),
        Field("workflow", OptionType(IntType),
          Some("id of the workflow"),
          resolve = _.value.workflow),
        Field("stepIndex", OptionType(IntType),
          Some("step index of the workflow execution"),
          resolve = _.value.stepIndex),
        Field("timestamp", OptionType(StringType),
          Some("timestamp"),
          resolve = _.value.timestamp.toString))
      )

  val ID = Argument("id", IntType, description = "id of the workflow/execution")
  val Steps = Argument("steps", IntType, description = "number of steps of the workflow")

  val LimitArg = Argument("limit", OptionInputType(IntType), defaultValue = 20)
  val OffsetArg = Argument("offset", OptionInputType(IntType), defaultValue = 0)

  val WorkflowArg = Argument("workflow", IntType, description = "id of the workflow")

  val Query = ObjectType(
    "Query", fields[TrayRepo, Unit](
      Field("workflow", OptionType(Workflow),
        arguments = ID :: Nil,
        resolve = (ctx) => ctx.ctx.getWorkflow(ctx arg ID)),
      Field("workflowExecution", OptionType(WorkflowExecution),
        arguments = ID :: Nil,
        resolve = (ctx) => ctx.ctx.getWorkflowExecution(ctx arg ID)),
      Field("workflows", ListType(Workflow),
        arguments = LimitArg :: OffsetArg :: Nil,
        resolve = ctx => ctx.ctx.getWorkflows(ctx arg LimitArg, ctx arg OffsetArg)),
      Field("workflowExecutions", ListType(WorkflowExecution),
        arguments = LimitArg :: OffsetArg :: Nil,
        resolve = ctx => ctx.ctx.getWorkflowExecutions(ctx arg LimitArg, ctx arg OffsetArg)),
    ))

  val MutationType = ObjectType("Mutation", fields[TrayRepo, Unit](
    Field("addWorkflow", OptionType(Workflow),
      arguments = ID :: Steps :: Nil,
      resolve = ctx => ctx.ctx.addWorkflow(ctx arg ID, ctx arg Steps)),
    Field("addWorkflowExecution", OptionType(WorkflowExecution),
      arguments = WorkflowArg :: Nil,
      resolve = ctx => ctx.ctx.addWorkflowExecution(ctx arg WorkflowArg)),
    Field("incrementWorkflowExecution", OptionType(WorkflowExecution),
      arguments = ID :: Nil,
      resolve = ctx => ctx.ctx.incrementWorkflowExecution(ctx arg ID))
  ))

  val TrayIoSchema = Schema(Query, Some(MutationType))
}
