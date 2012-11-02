package pl.mwojciec.generator.classes;

class RDFSyntax {

	//RDF Syntax keywords
	
	public static final String xmlHeader = "<?xml version=\"1.0\"?>";
	public static final String xmlNamespace = "xmlns:";
	public static final String rdfNamespace = "xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"";
	public static final String rdfHeader = "<rdf:RDF";
	public static final String rdfDescription = "<rdf:Description rdf:about=";
	public static final String rdfEnding = "</rdf:RDF>";
	public static final String rdfDescriptionEnding = "</rdf:Description>";
	public static final String rdfResource = "rdf:resource=";
	public static final String rdfDescriptionNodeId = "<rdf:Description rdf:nodeID=";
	public static final String rdfNodeId = "rdf:nodeID=";
	public static final String rdfBag = "<rdf:Bag>";
	public static final String rdfBagEnding = "</rdf:Bag>";
	public static final String rdfSeq = "<rdf:Seq>";
	public static final String rdfSeqEnding = "</rdf:Seq>";
	public static final String rdfAlt = "<rdf:Alt>";
	public static final String rdfAltEnding = "</rdf:Alt>";
	public static final String rdfCollection = "rdf:parseType=\"Collection\"";
	public static final String rdfID = "rdf:ID=\"";
	
	//RDFS Syntax Keywords
	public static final String rdfsNamespace = "xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"";
	public static final String rdfsClass = "<rdfs:Class rdf:ID=\"";
	public static final String rdfsClassEnding = "</rdfs:Class>";
	public static final String rdfsSubClass = "<rdfs:subClassOf rdf:resource=\"#";
	public static final String rdfsLabel = "<rdfs:label>";
	public static final String rdfsLabelEnding = "</rdfs:label>";
	public static final String rdfsComment = "<rdfs:comment>";
	public static final String rdfsCommentEnding = "</rdfs:comment>";
}
