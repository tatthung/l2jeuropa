/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ai;

import lineage2.gameserver.ai.CtrlIntention;
import lineage2.gameserver.ai.Guard;
import lineage2.gameserver.data.htm.HtmCache;
import lineage2.gameserver.model.Creature;
import lineage2.gameserver.model.Player;
import lineage2.gameserver.model.instances.MonsterInstance;
import lineage2.gameserver.model.instances.NpcInstance;
import lineage2.gameserver.network.serverpackets.NpcHtmlMessage;
import lineage2.gameserver.tables.SkillTable;

/**
 * @version $Revision: 1.0 $
 */
public class KartiaGuard extends Guard
{
	
	private long _ReuseTimer = 0;

	/**
	 * Constructor for FollowNpc.
	 * @param actor NpcInstance
	 */
	public KartiaGuard(NpcInstance actor)
	{
		super(actor);
	}
	
	/**
	 * Method onEvtThink.
	 */
	@Override
	protected void onEvtThink()
	{
		final NpcInstance actor = getActor();
		Creature master = getActor().getFollowTarget();
		//Check for Heal
		if (actor.getNpcId() == 33639 || actor.getNpcId() == 33628 || actor.getNpcId() == 33617)
		{
			if (master != null && !master.isDead() && master.getCurrentHpPercents() < 50)
			{
				if (!actor.isCastingNow() && (_ReuseTimer < System.currentTimeMillis()))
				{
					actor.doCast(SkillTable.getInstance().getInfo(698, 1), master, true);
					_ReuseTimer = System.currentTimeMillis() + (3 * 1000L);
				}
			}
		}
		if (actor.getAI().getIntention() != CtrlIntention.AI_INTENTION_ATTACK)
		{
			//Check for Mobs to Attack
			int mobscount = 0;
			for (NpcInstance npc : actor.getAroundNpc(600, 100))
			{
				if (npc instanceof MonsterInstance)
				{
					actor.getAggroList().addDamageHate(npc, 10, 10);
					mobscount++;
				}
			}
			if (mobscount > 0 && !actor.getAggroList().isEmpty())
			{
				Attack(actor.getAggroList().getRandomHated(), false, false);
			}
			//Check for Follow
			else if (master != null && master.getDistance(actor.getLoc()) > 600)
			{
				actor.setRunning();
				actor.followToCharacter(master, 120, false);
			}
		}
	}

}
