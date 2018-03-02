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
package org.droidmate.exploration.strategy.termination

import org.droidmate.configuration.Configuration
import org.droidmate.exploration.actions.TerminateExplorationAction
import org.droidmate.exploration.strategy.WidgetContext

/**
 * Determines if exploration shall be terminated based on the number of actions performed
 *
 * @author Nataniel P. Borges Jr.
 */
class ActionBasedTerminate(cfg: Configuration) : Terminate() {
    private val startingActionsLeft: Int
    private var actionsLeft: Int = 0

    init {
        if (cfg.widgetIndexes.isNotEmpty())
            this.startingActionsLeft = cfg.widgetIndexes.size
        else
            this.startingActionsLeft = cfg.actionsLimit

        this.actionsLeft = this.startingActionsLeft
    }

    override fun getLogMessage(): String {
        return "${this.startingActionsLeft - this.actionsLeft}/${this.startingActionsLeft}"
    }

    override fun start() {
        assert(actionsLeft >= 0)
    }

    override fun updateState(actionNr: Int) {
        super.updateState(actionNr)

        if (!this.memory.isEmpty()) {
            val selectedAction = this.memory.getLastAction()?.action!!
            actionsLeft--
            assert(actionsLeft >= 0 || actionsLeft == -1 && selectedAction is TerminateExplorationAction)
        }
    }

    override fun metReason(widgetContext: WidgetContext): String {
        return "No actions left."
    }

    override fun met(widgetContext: WidgetContext): Boolean {
        return actionsLeft <= 0
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ActionBasedTerminate)
            return false

        return this.startingActionsLeft == other.startingActionsLeft
    }

    override fun hashCode(): Int {
        return this.startingActionsLeft.hashCode()
    }
}