import java.sql.Timestamp
import java.time.LocalDateTime
import scala.collection.mutable._

case class Workflow(id: Int, steps: Int)

case class WorkflowExecution(
  id: Int,
  workflow: Int,
  stepIndex: Int,
  timestamp: Timestamp)


class TrayRepo {
  import TrayRepo._

  def getWorkflow(id: Int): Option[Workflow] =
    workflows.find(c ⇒ c.id == id)

  def getWorkflowExecution(id: Int): Option[WorkflowExecution] =
    workflowExecutions.find(c => c.id == id)

  def getWorkflows(limit: Int, offset: Int): List[Workflow] = 
    workflows.drop(offset).take(limit)

  def getWorkflowExecutions(limit: Int, offset: Int): List[WorkflowExecution] = 
    workflowExecutions.drop(offset).take(limit)

  def addWorkflow(id: Int, steps: Int): Option[Workflow] = {
    workflows = workflows :+ Workflow(id, steps)
    workflows.find(c => c.id == id)
  }

  def addWorkflowExecution(
    id: Int,
    workflow: Int,
    stepIndex: Int,
    timestamp: String)
  : Option[WorkflowExecution] = {
    val we = WorkflowExecution(id, workflow, stepIndex, Timestamp.valueOf(timestamp))
    workflowExecutions = workflowExecutions :+ we
    workflowExecutions.find(c => c.id == id)
  }
}

object TrayRepo {
  var workflows = List(
    Workflow(
      id = 0,
      steps = 1),
    Workflow(
      id = 1,
      steps = 1))
   var workflowExecutions = List(
     WorkflowExecution(
      id = 0,
      workflow = 0,
      stepIndex = 0,
      timestamp = Timestamp.valueOf(LocalDateTime.now)),
     WorkflowExecution(
      id = 1,
      workflow = 0,
      stepIndex = 0,
      timestamp = Timestamp.valueOf(LocalDateTime.now)))
}
