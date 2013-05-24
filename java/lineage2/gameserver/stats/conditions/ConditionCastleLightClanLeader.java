package lineage2.gameserver.stats.conditions;

import lineage2.gameserver.model.Player;
import lineage2.gameserver.stats.Env;

/**
 * User: DonTariel
 * Date: 03.02.13
 * Time: 10:46
 */
public class ConditionCastleLightClanLeader extends Condition
{
	private boolean light;

	public ConditionCastleLightClanLeader(boolean light)
	{
		this.light = light;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(!env.character.isPlayer())
		{
			return false;
		}

		Player player = env.character.getPlayer();

		if(player.getClan() == null || player.getCastle() == null)
		{
			return false;
		}

		if(!player.isClanLeader())
		{
			return false;
		}

		return (player.getCastle().isCastleTypeLight() && light) || (!player.getCastle().isCastleTypeLight() && !light);
	}
}