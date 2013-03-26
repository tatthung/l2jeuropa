package ai.EvilIncubator;

import java.util.List;

import lineage2.gameserver.ai.CtrlEvent;
import lineage2.gameserver.ai.Fighter;
import lineage2.gameserver.model.Creature;
import lineage2.gameserver.model.instances.NpcInstance;

/**
 * @author KilRoy
 */
public class EvilIncubatorMob extends Fighter
{
	public EvilIncubatorMob(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if (actor.isDead())
			return false;

			List<NpcInstance> arounds = actor.getAroundNpc(3000, 3000);
			if(arounds != null && !arounds.isEmpty())
				for(NpcInstance npc : arounds)
					if(npc.getNpcId() != 33416 && npc.isNpc() && !npc.isInFaction(actor))
						actor.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, npc, 300);

		return true;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		if(attacker == null)
			return;

		super.onEvtAttacked(attacker, damage);
	}

	@Override
	public int getMaxAttackTimeout()
	{
		return 0;
	}

	@Override
	protected boolean maybeMoveToHome()
	{
		return false;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}