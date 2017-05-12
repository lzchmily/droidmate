package org.droidmate.exploration.actions

import groovy.util.logging.Slf4j
import org.droidmate.android_sdk.IApk
import org.droidmate.android_sdk.DeviceException
import org.droidmate.exploration.device.DeviceLogsHandler
import org.droidmate.exploration.device.IDeviceLogsHandler
import org.droidmate.exploration.device.IRobustDevice

import java.time.LocalDateTime

import static org.droidmate.device.datatypes.AndroidDeviceAction.newEnterTextDeviceAction

@Slf4j
class RunnableEnterTextExplorationAction extends RunnableExplorationAction
{

  private static final long serialVersionUID = 1

  private final EnterTextExplorationAction action

  RunnableEnterTextExplorationAction(EnterTextExplorationAction action, LocalDateTime timestamp)
  {
    super(action, timestamp)
    this.action = action
  }

  protected void performDeviceActions(IApk app, IRobustDevice device) throws DeviceException
  {
    log.debug("1. Assert only background API logs are present, if any.")
    IDeviceLogsHandler logsHandler = new DeviceLogsHandler(device)
    logsHandler.readClearAndAssertOnlyBackgroundApiLogsIfAny()

    log.debug("2. Perform enter text: ${action}.")
    device.perform(newEnterTextDeviceAction(action.widget.resourceId, action.textToEnter))

    log.debug("3. Read and clear API logs if any, then seal logs reading.")
    logsHandler.readAndClearApiLogs()
    this.logs = logsHandler.getLogs()

    log.debug("4. Get GUI snapshot.")
    this.snapshot = device.guiSnapshot
  }

}

