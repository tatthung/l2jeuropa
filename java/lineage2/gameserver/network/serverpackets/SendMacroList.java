package lineage2.gameserver.network.serverpackets;

import lineage2.gameserver.model.actor.instances.player.Macro;

/**
 * packet type id 0xe7
 * <p/>
 * sample
 * <p/>
 * e7 d // unknown change of Macro edit,add,delete c // unknown c //count of Macros c // unknown
 * <p/>
 * d // id S // macro name S // desc S // acronym c // icon c // count
 * <p/>
 * c // entry c // type d // skill id c // shortcut id S // command name
 * <p/>
 * format: cdccdSSScc (ccdcS)
 */
public class SendMacroList extends L2GameServerPacket
{
	private final byte _counter;
	private final Macro[] _macros;

	public SendMacroList(byte counter, Macro[] macro)
	{
		_counter = counter;
		_macros = macro;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xe8);

		writeC(_counter);
		writeD(1);
		writeC(_macros.length); // count of Macros
		writeC(_macros.length); // unknown

		for (Macro _macro : _macros)
		{
			writeD(_macro.id); // Macro ID
			writeS(_macro.name); // Macro Name
			writeS(_macro.descr); // Desc
			writeS(_macro.acronym); // acronym
			writeC(_macro.icon); // icon

			writeC(_macro.commands.length); // count

			for (int i = 0; i < _macro.commands.length; i++)
			{
				Macro.L2MacroCmd cmd = _macro.commands[i];
				writeC(i + 1); // i of count
				writeC(cmd.type); // type 1 = skill, 3 = action, 4 = shortcut
				writeD(cmd.d1); // skill id
				writeC(cmd.d2); // shortcut id
				writeS(cmd.cmd); // command name
			}
		}
	}
}