package cz.cvut.fit.bap.parser.scrapper;

import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import cz.cvut.fit.bap.parser.scrapper.fetcher.AbstractFetcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for scrapping authority's completed list of procurements
 *
 * @see <a href="https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/MVCR/uzavrene-zakazky">contractor authority completed page</a>
 */
public class ContractorCompletedScrapper extends AbstractScrapper{
    private int pageIterator = 1;

    public ContractorCompletedScrapper(AbstractFetcher fetcher){
        super(fetcher);
    }

    /**
     * Gets completed procurement system numbers made by provided authority
     *
     * @param authority wanted contractor authority
     * @return List of completed procurement's system numbers
     */
    public List<String> getProcurementSystemNumbers(ContractorAuthority authority){
        List<String> procurementSystemNumbers = new ArrayList<>();
        while (fetchNextPage(authority)){
            procurementSystemNumbers.addAll(
                    document.select(".gov-table__cell--second.gov-table__cell").eachText());
        }
        return procurementSystemNumbers;
    }

    private boolean fetchNextPage(ContractorAuthority contractorAuthority){
        document = fetcher.getContractorCompleted(contractorAuthority.getProfile(),
                                                  contractorAuthority.getName(), pageIterator);
        if (document.select(".table-empty-message").isEmpty()){
            pageIterator++;
            return true;
        }
        return false;
    }
}
