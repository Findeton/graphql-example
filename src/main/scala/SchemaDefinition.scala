import sangria.execution.deferred.{Fetcher, HasId}
import sangria.schema._

import scala.concurrent.Future

/**
 * Defines a GraphQL schema for the current project
 */
object SchemaDefinition {
  /**
    * Resolves the lists of characters. These resolutions are batched and
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
      "A character in the Star Wars Trilogy",
      () => fields[TrayRepo, Workflow](
        Field("id", IntType,
          Some("The id of the character."),
          resolve = _.value.id),
        Field("steps", OptionType(IntType),
          Some("The name of the character."),
          resolve = _.value.steps))
      )

  val WorkflowExecution: ObjectType[TrayRepo, WorkflowExecution] =
    ObjectType(
      "WorkflowExecution",
      "A character in the Star Wars Trilogy",
      () => fields[TrayRepo, WorkflowExecution](
        Field("id", IntType,
          Some("The id of the character."),
          resolve = _.value.id),
        Field("workflow", OptionType(IntType),
          Some("The name of the character."),
          resolve = _.value.workflow),
        Field("stepIndex", OptionType(IntType),
          Some("The name of the character."),
          resolve = _.value.stepIndex),
        Field("timestamp", OptionType(StringType),
          Some("The name of the character."),
          resolve = _.value.timestamp.toString))
      )

  val ID = Argument("id", IntType, description = "id of the workflow/execution")
  val Steps = Argument("steps", IntType, description = "id of the workflow/execution")
  val LimitArg = Argument("limit", OptionInputType(IntType), defaultValue = 20)
  val OffsetArg = Argument("offset", OptionInputType(IntType), defaultValue = 0)

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
      resolve = ctx => ctx.ctx.addWorkflow(ctx arg ID, ctx arg Steps))
  ))

  val TrayIoSchema = Schema(Query, Some(MutationType))
}
