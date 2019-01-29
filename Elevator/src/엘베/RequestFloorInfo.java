package 엘베;

/**
* 호출,업/다운 요청의 상태를 가지는 클래스
* 객체로 생성되어 각 층(벡터)으로 저장된다.
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
