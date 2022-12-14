package pl.futurecollars.invoicing.db.file


import pl.futurecollars.invoicing.DataForTesting
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification

class JsonServiceTest extends Specification {

    def "can convert object to json and read it back"() {
        given:
        def jsonService = new JsonService()
        def invoice = DataForTesting.firstInvoice

        when:
        def invoiceAsString = jsonService.convertToJson(invoice)

        and:
        def invoiceFromJson = jsonService.convertToObject(invoiceAsString, Invoice)

        then:
        invoice.getId() == invoiceFromJson.getId()
        invoice.getBuyer().getAddress() == invoiceFromJson.getBuyer().getAddress()
        invoice.getSeller().getName() == invoiceFromJson.getSeller().getName()
        invoice.getDate() == invoiceFromJson.getDate()
    }

}
