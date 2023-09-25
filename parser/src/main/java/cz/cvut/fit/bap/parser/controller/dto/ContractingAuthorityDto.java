package cz.cvut.fit.bap.parser.controller.dto;

/*
    Record representing contracting authority data scrapped from procurement detail page
 */
public record ContractingAuthorityDto(
        String url,
        String name
){
}