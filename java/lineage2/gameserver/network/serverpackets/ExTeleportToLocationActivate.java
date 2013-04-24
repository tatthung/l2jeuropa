package lineage2.gameserver.network.serverpackets;

public class ExTeleportToLocationActivate extends L2GameServerPacket {

    @Override
    protected final void writeImpl() {
        writeEx(0x142);
    }
}