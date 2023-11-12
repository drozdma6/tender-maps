package cz.cvut.fit.bap.parser.controller.data;

/*
    Class represents data scrapped from procurement detail page about contact to contracting authority
 */
public record ContactPersonData(
        String name,
        String surname,
        String email
) {
}
