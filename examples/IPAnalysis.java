package examples;

import annotations.*;

public class IPAnalysis extends SXPathDriver {
	
	// the patent under consideration
	String patentCountry;
	String patentDocNo;
	String patentKind;
	
	@Trigger("//exch:exchange-document")
	public void matchPatent(
			@Select("@country") String country,
			@Select("@doc-number") String docNo,
			@Select("@kind") String kind) 
	{
		this.patentCountry = country;
		this.patentDocNo = docNo;
		this.patentKind = kind;
	}
	
    @Trigger("//exch:exchange-document[@country,@doc-number,@kind]//patcit/document-id")
	public void matchCitation(
			@Select("/country") String country,
			@Select("/doc-number") String docNo,
			@Select("/kind") String kind)
	{
    	System.out.println(patentCountry + "," + patentDocNo + "," + patentKind 
    			+ "," + country + "," + docNo + "," + kind);
	}
    
    public static void main(String[] args) {
    	for (String file : args) {
			try {
				(new IPAnalysis()).parseFile(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    }

}
