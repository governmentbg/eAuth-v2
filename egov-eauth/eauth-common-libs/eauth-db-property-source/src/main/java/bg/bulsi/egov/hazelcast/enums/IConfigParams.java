package bg.bulsi.egov.hazelcast.enums;

public interface IConfigParams {

	public String prefix();
	
	public String value();
	
	/*
	 * concat prefix + "." + value
	 */
	public String key();
}
