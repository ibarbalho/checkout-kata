import { Item } from "../services/item.service";
import { Offer } from "../services/offer.service";

export function formatOffer(item: Item, offers: Offer[]): string | undefined {
    const offer = offers.find(o => o.item.id === item.id);
    return offer ? `${offer.quantity} for ${offer.totalPrice}€` : undefined;
}
