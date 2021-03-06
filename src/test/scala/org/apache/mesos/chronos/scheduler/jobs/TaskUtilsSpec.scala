package org.apache.mesos.chronos.scheduler.jobs

import org.joda.time._
import org.specs2.mock._
import org.specs2.mutable._

class TaskUtilsSpec extends SpecificationWithJUnit with Mockito {

  "TaskUtils" should {
    "Get taskId" in {
      val schedule = "R/2012-01-01T00:00:01.000Z/P1M"
      val arguments = "-a 1 -b 2"
      val job1 = new ScheduleBasedJob(schedule, "sample-name", "sample-command", arguments = List(arguments))
      val job2 = new ScheduleBasedJob(schedule, "sample-name", "sample-command")
      val ts = 1420843781398L
      val due = new DateTime(ts)

      val taskIdOne = TaskUtils.getTaskId(job1, due, 0)
      val taskIdTwo = TaskUtils.getTaskId(job2, due, 0)

      taskIdOne must_== s"ct:1420843781398:0:sample-name:$arguments:"
      taskIdTwo must_== "ct:1420843781398:0:sample-name::"
    }

    "Get job arguments for taskId" in {
      val arguments = "-a 1 -b 2"
      var taskId = "ct:1420843781398:0:test:" + arguments
      val jobArguments = TaskUtils.getJobArgumentsForTaskId(taskId)

      jobArguments must_== arguments
    }

    "Parse taskId" in {
      val arguments = "-a 1 -b 2"
      val taskFlowId = "taskflow1"
      //job arguments cannot have ':' now...
      val arguments2 = "-a 1 2 --B test"
      val taskFlowId2 = "taskflow2"
      
      val taskIdOne = s"ct:1420843781398:0:test:$arguments:$taskFlowId"
      val (jobName, jobDue, attempt, jobArguments, taskFlow) = TaskUtils.parseTaskId(taskIdOne)

      jobName must_== "test"
      jobDue must_== 1420843781398L
      attempt must_== 0
      jobArguments must_== arguments
      taskFlow must_== taskFlowId

      val taskIdTwo = s"ct:1420843781398:0:test:$arguments2:$taskFlowId2"
      val (_, _, _, jobArguments2, taskFlow2) = TaskUtils.parseTaskId(taskIdTwo)

      jobArguments2 must_== arguments2
      taskFlow2 must_== taskFlowId2

      val taskIdThree = "ct:1420843781398:0:test"
      val (jobName3, _, _, jobArguments3, _) = TaskUtils.parseTaskId(taskIdThree)

      jobName3 must_== "test"
      jobArguments3 must_== ""
    }
  }
}

