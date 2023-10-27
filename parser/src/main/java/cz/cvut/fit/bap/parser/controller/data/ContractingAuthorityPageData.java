package cz.cvut.fit.bap.parser.controller.data;

/*
    Record representing contracting authority data scrapped from procurement detail page
 */
public record ContractingAuthorityPageData(
        String url,
        String name
){
}