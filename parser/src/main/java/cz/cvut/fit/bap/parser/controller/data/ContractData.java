package cz.cvut.fit.bap.parser.controller.data;

import cz.cvut.fit.bap.parser.controller.currency_exchanger.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;

/*
    Row from supplier's table on procurement result page.
 */
public record ContractData(
        BigDecimal price,
        BigDecimal priceVAT,
        BigDecimal priceWithAmend,
        BigDecimal priceWithAmendVAT,
        String detailHref,
        String companyName,
        Currency currency,
        LocalDate contractDate
) {
    /**
     * Finds if some of the contract prices is null
     *
     * @return true if some price is null, false otherwise
     */
    public boolean hasEmptyPrice() {
        return price() == null
                || priceVAT() == null
                || priceWithAmend() == null
                || priceWithAmendVAT() == null;
    }

    /**
     * Sum prices of contracts
     *
     * @param contract which prices are supposed to be added
     * @return new ContractData with summed prices
     */
    public ContractData sumContracts(ContractData contract) {
        return new ContractData(price().add(contract.price()),
                priceVAT().add(contract.priceVAT()),
                priceWithAmend().add(contract.priceWithAmend()),
                priceWithAmendVAT().add(contract.priceWithAmendVAT()),
                detailHref(),
                companyName(),
                currency(),
                contractDate()
        );
    }
}
