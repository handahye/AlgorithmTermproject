package ����;

/**
* ȣ��,��/�ٿ� ��û�� ���¸� ������ Ŭ����
* ��ü�� �����Ǿ� �� ��(����)���� ����ȴ�.
*/
class RequestFloorInfo
{
	private boolean Request = false;
	private boolean upRequest = false;
	private boolean downRequest = false;

	public void setRequest(boolean value)
	{
		this.Request = value;
	}

	public void setUpRequest(boolean value)
	{
		this.upRequest = value;
	}

	public void setDownRequest(boolean value)
	{
		this.downRequest = value;
	}

	public boolean getRequest()
	{
		return this.Request;
	}

	public boolean getUpRequest()
	{
		return this.upRequest;
	}

	public boolean getDownRequest()
	{
		return this.downRequest;
	}

}
