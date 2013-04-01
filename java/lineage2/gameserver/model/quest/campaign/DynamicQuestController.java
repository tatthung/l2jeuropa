package lineage2.gameserver.model.quest.campaign;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lineage2.commons.threading.RunnableImpl;
import lineage2.commons.time.cron.SchedulingPattern;
import lineage2.gameserver.ThreadPoolManager;
import lineage2.gameserver.model.Player;

public class DynamicQuestController
{
	private static final Logger log = LoggerFactory.getLogger(DynamicQuestController.class);
	private static DynamicQuestController ourInstance = new DynamicQuestController();
	private final Map<Integer, DynamicQuest> dynamicQuestsMap;
	private final Map<String, DynamicQuest> dynamicQuestsMapByName;
	
	private DynamicQuestController()
	{
		dynamicQuestsMap = new HashMap<>();
		dynamicQuestsMapByName = new HashMap<>();
	}
	
	public static DynamicQuestController getInstance()
	{
		return ourInstance;
	}
	
	public void registerDynamicQuest(DynamicQuest dynamicQuest)
	{
		dynamicQuestsMap.put(dynamicQuest.getQuestId(), dynamicQuest);
		dynamicQuestsMapByName.put(dynamicQuest.getClass().getSimpleName(), dynamicQuest);
	}
	
	/**
	 * Starts dynamic quest (campaign / radar quest)
	 * The specified questId and step to 1
	 * @param questId - quest ID
	 */
	public void startQuest(int questId)
	{
		startQuest(questId, 1);
	}
	
	/**
	 * Starts dynamic quest (campaign / radar quest)
	 * With a given step and questId
	 * @param questId - quest ID
	 * @param step    - started quest step
	 */
	public void startQuest(int questId, int step)
	{
		DynamicQuest quest = dynamicQuestsMap.get(questId);
		quest.setCurrentStep(step);
		if(quest.isStartCondition())
			quest.start(new QuestEnder(questId));
		//System.out.println("Dynamic Quest Id: " + questId + " - [Started]");
	}
	
	/**
	 * @param questId - quest ID
	 * @param success - set finished quest with success (true or false)
	 */
	public void endQuest(int questId, boolean success)
	{
		DynamicQuest quest = dynamicQuestsMap.get(questId);
		quest.stop(success, new QuestFinalizer(questId));
	}
	
	/**
	 * @param questId - quest ID
	 */
	public void finalizeQuest(int questId)
	{
		DynamicQuest quest = dynamicQuestsMap.get(questId);
		quest.finish();
	}
	
	/**
	 * @param questId
	 * @param pattern
	 */
	public void initSchedulingPattern(int questId, SchedulingPattern pattern)
	{
		if(!dynamicQuestsMap.containsKey(questId))
		{
			log.warn("DynamicQuestController#initSchedulingPattern(int, SchedulingPattern): Not found quest with id: " + questId);
			return;
		}
		long nextLaunchTime = pattern.next(System.currentTimeMillis());
		ThreadPoolManager.getInstance().schedule(new QuestStarter(questId, pattern), nextLaunchTime - System.currentTimeMillis());
	}
	
	public void requestDynamicQuestHtml(int id, int step, Player player)
	{
		if(dynamicQuestsMap.containsKey(id))
		{
			DynamicQuest quest = dynamicQuestsMap.get(id);
			quest.requestHtml(step, player);
		}
	}

	public void requestQuestProgressInfo(int id, int step, Player player)
	{
		if(dynamicQuestsMap.containsKey(id))
		{
			DynamicQuest quest = dynamicQuestsMap.get(id);
			quest.requestProgressInfo(step, player);
		}
	}

	public void requestScoreBoard(int id, int step, Player player)
	{
		if(dynamicQuestsMap.containsKey(id))
		{
			DynamicQuest quest = dynamicQuestsMap.get(id);
			quest.requestScoreBoard(step, player);
		}
	}

	public void taskCompleted(int questId, int taskId)
	{
		if(dynamicQuestsMap.containsKey(questId))
		{
			DynamicQuest quest = dynamicQuestsMap.get(questId);
			quest.taskCompleted(taskId);
		}
	}

	public void processDialogEvent(String questName, String event, Player player)
	{
		if(dynamicQuestsMapByName.containsKey(questName))
		{
			DynamicQuest quest = dynamicQuestsMapByName.get(questName);
			quest.processDialogEvent(event, player);
		}
	}
	
	public void StartCondition(int questId)
	{
		if(dynamicQuestsMap.containsKey(questId))
		{
			DynamicQuest quest = dynamicQuestsMap.get(questId);
			quest.isStartCondition();
		}
	}

	private final class QuestStarter extends RunnableImpl
	{
		private final int questId;
		private final SchedulingPattern pattern;

		public QuestStarter(int questId, SchedulingPattern pattern)
		{
			this.questId = questId;
			this.pattern = pattern;
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public void runImpl()
		{
			DynamicQuest quest = dynamicQuestsMap.get(questId);
			startQuest(questId);
			long nextLaunchTime = pattern.next(System.currentTimeMillis() + quest.getDuration() * 1000);
			ThreadPoolManager.getInstance().schedule(this, nextLaunchTime - System.currentTimeMillis());
		}
	}

	private final class QuestEnder extends RunnableImpl
	{
		private final int questId;

		public QuestEnder(int questId)
		{
			this.questId = questId;
		}

		@Override
		public void runImpl()
		{
			endQuest(questId, false);
		}
	}

	private final class QuestFinalizer extends RunnableImpl
	{
		private final int questId;

		public QuestFinalizer(int questId)
		{
			this.questId = questId;
		}

		@Override
		public void runImpl()
		{
			finalizeQuest(questId);
		}
	}
}
