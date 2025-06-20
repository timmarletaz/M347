export interface Poll {
  id: number;
  title: string;
  uuid: string;
  description: string;
  creator: {
    firstname: string;
    lastname: string;
    email: string;
  }
  elements: [
    {
      id: number
      label: string;
      placeholder: string,
      type: string,
      required: boolean
    }
  ];
}

export interface PollPreview {
  id: number;
  title: string;
  description: string;
  uuid: string;
  creator: {
    firstname: string;
    lastname: string;
    email: string;
  }
}

export interface PollDetails {
  elements: {
    element: {
      id: number;
      label: string;
      placeholder: string;
      type: string;
      required: boolean;
    };
    topAnswers: {
      answer: {
        id: number;
        count: number;
        value: string;
      };
      count: number;
    }[];
  }[];
}
