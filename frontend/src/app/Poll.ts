export interface Poll {
  title: string;
  description: string;
  elements: [
    { label: string;
      placeholder: string,
      type: string,
      required: boolean
    }
  ];
}
