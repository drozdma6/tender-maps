package cz.cvut.fit.bap.parser.controller.dto;

/*
    Record representing contractor authority data scrapped from procurement detail page
 */
public record ContractorAuthorityDto(
        String url,
        String name
){
}