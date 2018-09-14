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
    workflows.find(c => c.id == id)

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

  def addWorkflowExecution(workflow: Int)
  : Option[WorkflowExecution] = {
    val we = WorkflowExecution(workflowExecutions.length, workflow, 0, Timestamp.valueOf(LocalDateTime.now))
    workflowExecutions = workflowExecutions :+ we
    Some(workflowExecutions(we.id))
  }

  def incrementWorkflowExecution(id: Int)
  : Option[WorkflowExecution] = {
    var we = workflowExecutions(id)
    val w = workflows(we.workflow)
    if (w.steps > we.stepIndex + 1) {
      workflowExecutions = workflowExecutions.map( el =>
        if (el.id == id) {
          el.copy(stepIndex = (el.stepIndex + 1), timestamp = Timestamp.valueOf(LocalDateTime.now))
        } else {
          el
        }
      )
    }
    Some(workflowExecutions(id))
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
