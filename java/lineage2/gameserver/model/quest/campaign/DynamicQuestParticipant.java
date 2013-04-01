package lineage2.gameserver.model.quest.campaign;

public class DynamicQuestParticipant implements Comparable<DynamicQuestParticipant>
{
	private String _name;
	private int _currentPoints;
	private int _additionalPoints;

	public DynamicQuestParticipant(String name)
	{
		_name = name;
	}

	public String getName()
	{
		return _name;
	}

	public int getCurrentPoints()
	{
		return _currentPoints;
	}

	public int getAdditionalPoints()
	{
		return _additionalPoints;
	}

	public void setAdditionalPoints(int additionalPoints)
	{
		_additionalPoints = additionalPoints;
	}

	public void increaseCurrentPoints(int points)
	{
		_currentPoints += points;
	}

	@Override
	public int compareTo(DynamicQuestParticipant participant)
	{
		if (getCurrentPoints() + getAdditionalPoints() > participant.getCurrentPoints() + participant.getAdditionalPoints())
		{
			return 1;
		}
		else if (getCurrentPoints() + getAdditionalPoints() > participant.getCurrentPoints() + participant.getAdditionalPoints())
		{
			return 0;
		}
		else
		{
			return -1;
		}
	}
}