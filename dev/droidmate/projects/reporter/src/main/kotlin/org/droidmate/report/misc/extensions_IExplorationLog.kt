// DroidMate, an automated execution generator for Android apps.
// Copyright (C) 2012-2018 Konrad Jamrozik
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
// email: jamrozik@st.cs.uni-saarland.de
// web: www.droidmate.org
package org.droidmate.report.misc

import org.droidmate.apis.IApiLogcatMessage
import org.droidmate.device.datatypes.Widget
import org.droidmate.device.datatypes.statemodel.emptyUUID
import org.droidmate.exploration.actions.ResetAppExplorationAction
import org.droidmate.exploration.data_aggregators.IExplorationLog
import java.util.*

val IExplorationLog.uniqueActionableWidgets: Set<Widget>
  get() = mutableSetOf<Widget>().apply {
    getRecords().getWidgets().filter { it.canBeActedUpon() }.groupBy { it.uid } // TODO we would like a mechanism to identify which widget config was the (default)
        .forEach{ add(it.value.first()) }
  }

val IExplorationLog.uniqueClickedWidgets: Set<Widget>
  get() = mutableSetOf<Widget>().apply {
    actionTrace.getActions().forEach { action -> action.targetWidget?.let { add(it) } }
  }

//TODO not sure about the original intention of this function
val IExplorationLog.uniqueApis: Set<IApiLogcatMessage>
  get() = uniqueEventApiPairs.map { (_, api) -> api }.toSet()

val IExplorationLog.uniqueEventApiPairs: Set<Pair<UUID,IApiLogcatMessage>>
  get() = mutableSetOf<Pair<UUID,IApiLogcatMessage>>().apply {
    actionTrace.getActions().forEach {
      apiLogs.forEach { apiList -> apiList.forEach { api -> add(Pair(it.targetWidget?.uid?: emptyUUID, api)) } }
    }
  }

val IExplorationLog.resetActionsCount: Int
  get() = actionTrace.getActions().count { it.actionType == ResetAppExplorationAction::class.simpleName }

val IExplorationLog.apkFileNameWithUnderscoresForDots: String
  get() = apk.fileName.replace(".", "_")