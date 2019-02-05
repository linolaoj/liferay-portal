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
	'Options',
	() => {
        beforeEach(() => jest.useFakeTimers());

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

        it(
			'should allow the user to order the fieldName options by dragging and dropping the options',
			() => {
				component = new Options(
                    {
                        value: valueOptions,
                        spritemap
                    }
                );

				const spy = jest.spyOn(component, 'emit');

				const source = {
                    dataset: {
                        index: '1'
                    }
                };

				const target = {
                    dataset: {
                        index: '0'
                    }
                };

                jest.runAllTimers();

                component._handleDragAndDropEnd({source, target});

                jest.runAllTimers();

                expect(component).toMatchSnapshot();

                expect(spy).toHaveBeenCalledWith('fieldEdited', expect.anything());
			}
		);
    }
);