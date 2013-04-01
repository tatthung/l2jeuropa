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
package lineage2.gameserver.network.serverpackets;

import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lineage2.gameserver.model.quest.campaign.DynamicQuestParticipant;
import lineage2.gameserver.model.quest.campaign.DynamicQuestTask;

public class ExDynamicQuestPacket extends L2GameServerPacket
{
	static final Logger _log = LoggerFactory.getLogger(ExDynamicQuestPacket.class);
	private final DynamicQuestInfo questInfo;
	
	public ExDynamicQuestPacket(DynamicQuestInfo questInfo)
	{
		this.questInfo = questInfo;
	}
	

	@Override
	protected void writeImpl()
	{
		writeEx(0xE8);
		writeC(questInfo.questType);// isCampaign: 0 - Campaign, 1 - Zone Quest,
		writeC(questInfo.subType);	// subType: 0 - start, 1 - end, 2 - progress, 3 - statistic
		writeD(questInfo.questId);	// campaignId
		writeD(questInfo.step);		// step
		questInfo.write(this);
	}
	
	public static class DynamicQuestInfo 
	{
		public int questType;
		public int subType;
		public int questId;
		public int step;

		public DynamicQuestInfo(int subType)
		{
			this.subType = subType;
		}

		public void write(ExDynamicQuestPacket packet)
		{
			// Override
		}
	}
	
	public static class StartedQuest extends DynamicQuestInfo
	{
		private int state;
		private int remainingTime;
		private int participantsCount;
		private Collection<DynamicQuestTask> tasks = Collections.emptyList();

		public StartedQuest(int state, int remainingTime, int participantsCount, Collection<DynamicQuestTask> tasks)
		{
			super(2);
			this.state = state;
			this.remainingTime = remainingTime;
			this.participantsCount = participantsCount;
			this.tasks = tasks;
		}
		
		@Override
		public void write(ExDynamicQuestPacket packet)
		{
			packet.writeC(state);
			packet.writeD(remainingTime);
			if (questType == 1)  // zone quest
			{
				packet.writeD(participantsCount);
			}
			packet.writeD(tasks.size());
			
			for (DynamicQuestTask task : tasks)
			{
				packet.writeD(task.taskId);  //task.taskId
				packet.writeD(task.getCurrentPoints());
				packet.writeD(task.getMaxPoints());
			}
		}
	}
	
	public static class ScoreBoardInfo extends DynamicQuestInfo
	{
		private final int remainingTime;
		private final int friendsCount;
		private final Collection<DynamicQuestParticipant> participants;

		public ScoreBoardInfo(int remainingTime, int friendsCount, Collection<DynamicQuestParticipant> participants)
		{
			super(3);
			this.remainingTime = remainingTime;
			this.friendsCount = friendsCount;
			this.participants = participants;
		}

		@Override
		public void write(ExDynamicQuestPacket packet)
		{
			if (questType == 1)
			{
				packet.writeD(remainingTime); // remaining time
				packet.writeD(friendsCount); // party members count
				packet.writeD(participants.size()); // participants size
				for (DynamicQuestParticipant participant : participants)
				{
					packet.writeS(participant.getName());
					packet.writeD(participant.getCurrentPoints());// current points
					packet.writeD(participant.getAdditionalPoints()); // additional
					packet.writeD(participant.getCurrentPoints() + participant.getAdditionalPoints()); // total
				}
			}
			else
			{
				packet.writeD(participants.size()); // participants size
				for (DynamicQuestParticipant participant : participants)
				{
					packet.writeS(participant.getName()); // name
				}
			}
		}
	}
}
