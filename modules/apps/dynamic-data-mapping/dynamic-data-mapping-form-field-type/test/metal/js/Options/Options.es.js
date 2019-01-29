import Options from 'source/Options/Options.es';
import {dom as MetalTestUtil} from 'metal-dom';

let component;
const spritemap = 'icons.svg';

const valueOptions = {
    en_US: [
        {
            label: "Option 1",
            value: "Option1"
        },
        {
            label: "Option 2",
            value: "Option2"
        }
    ]
};

describe(
	'Select',
	() => {
		afterEach(
			() => {
				if (component) {
					component.dispose();
				}
			}
        );

        it(
			'should show the options',
			() => {
				component = new Options(
                    {
                        value: valueOptions,
                        spritemap
                    }
				);

				expect(component).toMatchSnapshot();
			}
        );

        it(
			'should delete an option when delete button is clicked',
			() => {
				component = new Options(
                    {
                        value: valueOptions,
                        spritemap
                    }
                );

                let before = component.items.length;

                MetalTestUtil.triggerEvent(
					component.element.querySelectorAll('.close')[1],
					'click',
					{}
                );

				expect(component.items.length).toEqual(before - 1);
			}
		);
    }
);